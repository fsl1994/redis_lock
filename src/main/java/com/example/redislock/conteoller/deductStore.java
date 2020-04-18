package com.example.redislock.conteoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class deductStore {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/deductStore")
    public String deductStore(){
        String lockKey = "lockKey";
        String uuid = UUID.randomUUID().toString();
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 10, TimeUnit.SECONDS);
        if (!result){
            return "error";
        }
        try {
            int store = Integer.parseInt(stringRedisTemplate.opsForValue().get("store"));
            if (store>0){
                int realStore = store -1;
                stringRedisTemplate.opsForValue().set("store",realStore+"");
                System.out.println("减扣成功,剩余库存:"+realStore);
            }else {
                System.out.println("减扣失败,库存不足");
            }
        }finally {
            if (uuid.equals(stringRedisTemplate.opsForValue().get(lockKey))){
                stringRedisTemplate.delete(lockKey);
            }
        }
        return "success";
    }

}
