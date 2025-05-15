package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TTL;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ShopTypeMapper shopTypeMapper;

    @Override
    public List<ShopType> queryShopType() {
        String key = CACHE_SHOP_TYPE;
        // 1.从Redis中查询店铺类型缓存
        List<String> list = stringRedisTemplate.opsForList().range(key,0, -1);
        // 2.判断是否存在
        if (list != null && !list.isEmpty()) {
            // 3.存在，直接返回
            List<ShopType> shopList = new ArrayList<>();
            for (String s : list) {
                ShopType shop = JSONUtil.toBean(s, ShopType.class);
                shopList.add(shop);
            }
            return shopList;
        }
        // 3.不存在，查询数据库
        List<ShopType> shopList = shopTypeMapper.getShopList();
        // 4.存在，写入Redis
        if (shopList != null && !shopList.isEmpty()) {
            for (ShopType shop : shopList) {
                stringRedisTemplate.opsForList().rightPush(key, JSONUtil.toJsonStr(shop));
            }
            stringRedisTemplate.expire(key, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        }
        // 5.返回
        return shopList;
    }
}
