package com.fishingboat.monitoring.domain.cache.repository;

import com.fishingboat.monitoring.domain.cache.dto.CachedDataDTO;
import com.fishingboat.monitoring.domain.cache.vo.ValueVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CacheRepositoryRedisTest {

    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void save() {
        //given
        String id = "cpu_usage";
        String time = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString().substring(0,16);
        String value = "95";
        CachedDataDTO build = CachedDataDTO.builder()
                .domain(id)
                .time(time)
                .value(value)
                .build();
        //when
        cacheRepository.save(build);
        //then
        String val = redisTemplate.opsForValue().get(build.getKey()).toString();
        Assertions.assertThat(val).isEqualTo(value);
        //after
        redisTemplate.delete(build.getKey());
    }

    @Test
    void findById() {
        //given
        String id = "cpu_usage/2018-03-11T23:23";
        String value = "90";
        redisTemplate.opsForValue().set(id, value);

        //when
        Optional<ValueVO> byId = cacheRepository.findById(id);

        //then
        Assertions.assertThat(byId.isEmpty()).isEqualTo(false);
        Assertions.assertThat(byId.get().getValue()).isEqualTo(value);

        //after
        redisTemplate.delete(id);
    }

    @Test
    void findByDomain() {
        //given
        String domain = "memory_usage";
        String date = "2022-02-02T23:2";
        for(int i=0; i<10; i++){
            String id = domain+"/"+date+Integer.toString(i);
            redisTemplate.opsForValue().set(id, "90");
        }
        //when
        Optional<List<ValueVO>> valueList = cacheRepository.findByDomain(domain);
        //then
        Assertions.assertThat(valueList.isEmpty()).isEqualTo(false);
        Assertions.assertThat(valueList.get().size()).isEqualTo(10);

        for (ValueVO valueVO : valueList.get()) {
            Assertions.assertThat(valueVO.getValue()).isEqualTo("90");
        }
        //after
        for(int i=0; i<10; i++){
            String id = domain+"/"+date+Integer.toString(i);
            redisTemplate.delete(id);
        }
    }

    @Test
    void countByDomain() {
        //given
        String domain = "memory_usage";
        String date = "2022-02-02T23:2";
        for(int i=0; i<10; i++){
            String id = domain+"/"+date+Integer.toString(i);
            redisTemplate.opsForValue().set(id, "90");
        }
        //when
        Long cnt = cacheRepository.countByDomain(domain);
        System.out.println(cnt);
        //then
        Assertions.assertThat(cnt).isEqualTo(10);
        //after
        for(int i=0; i<10; i++){
            String id = domain+"/"+date+Integer.toString(i);
            redisTemplate.delete(id);
        }
    }

    @Test
    void delete() {
        //given
        String id = "cpu_usage";
        String time = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString().substring(0,16);
        String value = "95";
        redisTemplate.opsForValue().set(id+"/"+time, value);
        //when
        cacheRepository.delete(CachedDataDTO.builder().domain("cpu_usage").time(time).build());
        //then
        Assertions.assertThatThrownBy(() -> {
            redisTemplate.opsForValue().get(id).toString();
        }).isInstanceOf(NullPointerException.class);
        //after
        redisTemplate.delete(id);
    }

    @Test
    void deleteByDomain() {
        //given
        String domain = "memory_usage";
        String date = "2022-02-02T23:2";
        for(int i=0; i<10; i++){
            String id = domain+"/"+date+Integer.toString(i);
            redisTemplate.opsForValue().set(id, "90");
        }
        //when
        cacheRepository.deleteByDomain(domain);
        //then
        for (int i = 0; i < 10; i++) {
            String id = domain+"/"+date+Integer.toString(i);
            Assertions.assertThatThrownBy(() -> {
                redisTemplate.opsForValue().get(id).toString();
            }).isInstanceOf(NullPointerException.class);
        }
    }
}