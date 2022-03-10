package net.invaco.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 */
@Service
public class TestService2 {

    private final Logger log = LoggerFactory.getLogger(TestService2.class);

    @Autowired
    @Qualifier("redisTemplateSecondary")
    private RedisTemplate<String, Object> redisTemplateSecondary;

    @Autowired
    @Qualifier("redisTemplate2")
    private StringRedisTemplate redisTemplate2;

    public void test2() {
        String t2 = (String) redisTemplateSecondary.opsForHash().get("test", "t1");

        log.warn("t2" + t2);

        String t22 = (String) redisTemplate2.opsForHash().get("test", "t11");

        log.warn("t22 " + t22);
    }
}
