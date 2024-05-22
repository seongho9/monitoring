package com.fishingboat.monitoring.domain.cache.repository;

import com.fishingboat.monitoring.domain.cache.dto.CachedDataDTO;
import com.fishingboat.monitoring.domain.cache.vo.ValueVO;

import java.util.List;
import java.util.Optional;

public interface CacheRepository {

    void save(CachedDataDTO cachedDataDTO);

    Optional<ValueVO> findById(String id);

    Optional<List<ValueVO>> findByDomain(String domain);

    Long countByDomain(String domain);

    void delete(CachedDataDTO cachedDataDTO);
    boolean deleteByDomain(String domain);
}
