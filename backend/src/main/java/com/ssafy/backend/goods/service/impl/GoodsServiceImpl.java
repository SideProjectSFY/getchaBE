package com.ssafy.backend.goods.service.impl;

import com.ssafy.backend.goods.model.GoodsMapper;
import com.ssafy.backend.goods.model.GoodsRequestDto;
import com.ssafy.backend.goods.model.GoodsResponseDto;
import com.ssafy.backend.goods.service.GoodsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;

    @Override
    public boolean addGoods(GoodsRequestDto.GoodsRegister goodsRegister, MultipartFile[] files) {
        return false;
    }

    @Override
    public List<GoodsResponseDto.GoodsCardVO> getAllGoods() {
        return List.of();
    }

    @Override
    public GoodsResponseDto.GoodsDetailAllVO getGoodsById(Long goodsId) {

        return null;
    }

    @Override
    public boolean updateGoods(GoodsRequestDto.GoodsModify goodsModify, MultipartFile[] files) {
        return false;
    }

    @Override
    public boolean deleteGoods(Long goodsId) {
        return false;
    }
}
