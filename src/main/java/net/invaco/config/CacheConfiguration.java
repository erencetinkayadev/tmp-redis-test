package net.invaco.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    private RedisProperties redisProperties;

    /*  @Primary
    @Bean(name = "lettucePrimaryReadWriteConnectionFactory")
    public LettuceConnectionFactory lettucePrimaryReadWriteConnectionFactory() {
        LettuceConnectionFactory lcf = new LettuceConnectionFactory(getStandaloneConfig(redisProperties));
        lcf.setShareNativeConnection(false);
        lcf.afterPropertiesSet();
        return lcf;
    }

    @Bean(name = "lettuceSecondaryReadWriteConnectionFactory")
    public LettuceConnectionFactory lettuceSecondaryReadWriteConnectionFactory() {
        LettuceConnectionFactory lcf = new LettuceConnectionFactory(getStandaloneConfig(redisProperties));
        lcf.setShareNativeConnection(false);
        lcf.afterPropertiesSet();
        return lcf;
    }*/

    /*  @Primary
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        LettuceConnectionFactory lcf = new LettuceConnectionFactory(getStandaloneConfig(redisProperties));
        lcf.setShareNativeConnection(false);
        lcf.afterPropertiesSet();
        return lcf;
    }*/

    @Bean(name = "redisTemplate1")
    public StringRedisTemplate redisTransactionTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        // explicitly enable transaction support
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean(name = "redisTemplate2")
    public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        return template;
    }

    @Primary
    @Bean(name = "redisTemplatePrimary")
    public RedisTemplate<String, Object> redisTemplatePrimary(
        /* @Qualifier("lettucePrimaryReadWriteConnectionFactory") */RedisConnectionFactory connectionFactory,
        ObjectMapper objectMapper,
        JavaTimeModule javaTimeModule
    ) {
        objectMapper = objectMapperConfigure(objectMapper, javaTimeModule);

        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "redisTemplateSecondary")
    public RedisTemplate<String, Object> redisTemplateSecondary(
        /* @Qualifier("lettuceSecondaryReadWriteConnectionFactory") */RedisConnectionFactory connectionFactory,
        ObjectMapper objectMapper,
        JavaTimeModule javaTimeModule
    ) {
        objectMapper = objectMapperConfigure(objectMapper, javaTimeModule);

        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setEnableTransactionSupport(false);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }

    @Autowired(required = false)
    public void setRedisProperties(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    private RedisStandaloneConfiguration getStandaloneConfig(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        config.setDatabase(redisProperties.getDatabase());
        return config;
    }

    private ObjectMapper objectMapperConfigure(ObjectMapper objectMapper, JavaTimeModule javaTimeModule) {
        objectMapper = objectMapper.copy();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        //dateTime , JSR310 LocalDateTimeSerializer
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        //, LocalDateTIme LocalDate , Jackson-data-JSR310
        objectMapper.registerModule(javaTimeModule);
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        return objectMapper;
    }
}
