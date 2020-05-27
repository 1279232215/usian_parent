package com.usian.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
 * 完成对Redis的整合的一些配置
 * */
@Configuration
public class RedisConfig {
    /*
     * 创建redisTemplate:用于执行redis操作的方法
     * */
    @Bean
    public RedisTemplate<String,Object> getRedisTemplate(RedisConnectionFactory factory){
        //创建RedisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        //创建Redis中的key序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //创建Redis中的value序列化器
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();

        //指定要序列化的field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类比如String,Integer等会抛出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        //设置key:value和hashKey:hashValue的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        //初始实例化redisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
