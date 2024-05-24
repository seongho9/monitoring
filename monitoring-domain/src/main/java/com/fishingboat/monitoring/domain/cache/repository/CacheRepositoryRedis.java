package com.fishingboat.monitoring.domain.cache.repository;

import com.fishingboat.monitoring.domain.cache.config.MonitoringConfig;
import com.fishingboat.monitoring.domain.cache.dto.CachedDataDTO;
import com.fishingboat.monitoring.domain.cache.vo.ValueVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class CacheRepositoryRedis implements CacheRepository {

    private final RedisTemplate<String, String> _RedisTemplate;
    private final String delimiter;

    public CacheRepositoryRedis(
            @Autowired RedisTemplate<String, String> redisTemplate,
            @Autowired MonitoringConfig monitoringInfo
            ) {
        this._RedisTemplate = redisTemplate;
        this.delimiter = monitoringInfo.getDelimiter();
    }

    @Override
    public void save(CachedDataDTO cachedDataDTO) {
        cachedDataDTO.setDelimiter(delimiter);
        _RedisTemplate.opsForValue().set(cachedDataDTO.getKey(), cachedDataDTO.getValue());
    }

    @Override
    public Optional<ValueVO> findById(String id) {
        String value = _RedisTemplate.opsForValue().get(id);

        if(value == null){
            return Optional.empty();
        }
        else{
            return Optional.of(
                    new ValueVO(value)
            );
        }
    }

    @Override
    public Optional<List<ValueVO>> findByDomain(String domain) {

        ThreadLocal<List<ValueVO>> valueLocal = ThreadLocal.withInitial(ArrayList::new);

        valueLocal.set(
                _RedisTemplate.execute((RedisCallback<List<ValueVO>>) connection -> {
                    ScanOptions opt = ScanOptions.scanOptions().match(domain + "*").build();
                    Cursor<byte[]> cursor = connection.keyCommands().scan(opt);

                    return cursor
                            .stream().map(key -> {
                                byte[] value = connection.commands().get(key);
                                String val = new String(value);
                                return new ValueVO(val);
                            }).collect(Collectors.toList());
                })
        );

        if(valueLocal.get().size()==0){
            return Optional.empty();
        }
        else{
            return Optional.of(valueLocal.get());
        }
    }

    @Override
    public Long countByDomain(String domain) {
        ThreadLocal<Long> key = ThreadLocal.withInitial(() -> 0L);

        _RedisTemplate.execute((RedisCallback<Object>) connection -> {
            ScanOptions opt = ScanOptions.scanOptions().match(domain + "*").build();
            Cursor<byte[]> cursor = connection.keyCommands().scan(opt);

            while (cursor.hasNext()) {
                Long l = key.get();
                key.set(++l);
                byte[] next = cursor.next();
            }
            return null;
        });

        return key.get();
    }

    @Override
    public void delete(CachedDataDTO cachedDataDTO) {
        log.info("id : {}", cachedDataDTO.getKey());

        _RedisTemplate.delete(cachedDataDTO.getKey());
    }

    @Override
    public boolean deleteByDomain(String domain) {
        ThreadLocal<Set<String>> keyLocal = ThreadLocal.withInitial(TreeSet::new);

        //domain 해당하는 key 값들 가져오기
        keyLocal.set(
                _RedisTemplate.execute((RedisCallback<Set>) connection -> {
                    ScanOptions opt = ScanOptions.scanOptions().match(domain + "*").build();
                    Cursor<byte[]> cursor = connection.keyCommands().scan(opt);

                    return cursor.stream().map(String::new).collect(Collectors.toSet());
                })
        );

        // 삭제는 1개의 트랜젝션 안에서
        // 트랜젝션 내에서는 scan 불가능
        List<Boolean> executedList = _RedisTemplate.execute(new SessionCallback<List<Boolean>>() {
            @Override
            public <K, V> List<Boolean> execute(RedisOperations<K, V> operations) throws DataAccessException {

                List<Boolean> valList = keyLocal.get().stream().map(key -> operations.delete((K) key))
                        .collect(Collectors.toList());

                return valList;
            }
        });
        if(executedList.stream().anyMatch(executed -> executed.equals(true))){
            return false;
        }
        else{
            return true;
        }
    }
}
