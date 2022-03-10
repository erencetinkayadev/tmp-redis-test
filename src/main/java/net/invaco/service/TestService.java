package net.invaco.service;

import java.util.Set;
import net.invaco.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TestService {

    private final Logger log = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("redisTemplatePrimary")
    private RedisTemplate<String, Object> redisTemplatePrimary;

    @Autowired
    private TestService2 testService2;

    @Autowired
    @Qualifier("redisTemplate1")
    private StringRedisTemplate redisTemplate1;

    @Autowired
    @Qualifier("redisTemplate2")
    private StringRedisTemplate redisTemplate2;

    public void test() {
        redisTemplatePrimary.opsForHash().put("test", "t1", "test1");

        redisTemplate1.opsForHash().put("test", "t11", "test1");

        String t1 = (String) redisTemplatePrimary.opsForHash().get("test", "t1");
        log.warn("t1 :" + t1);

        String t11 = (String) redisTemplate1.opsForHash().get("test", "t11");
        log.warn("t11 :" + t11);

        testService2.test2();
        /*  redisTemplate1.opsForValue().set("thing1", "thing2");

        // read operation must be run on a free (not transaction-aware) connection
        Set<String> keys= redisTemplate1.keys("*");

        // returns null as values set within a transaction are not visible
        String t4 = redisTemplate1.opsForValue().get("thing1");
        log.warn("t4 :" + t4);


        String t5 = redisTemplate2.opsForValue().get("thing1");
        log.warn("t5 :" + t5);
        Set<String> keys2= redisTemplate2.keys("*");

      */
    }
}
