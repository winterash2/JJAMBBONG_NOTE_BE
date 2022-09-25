package com.jjambbong.note.serviceImpl;

import com.jjambbong.note.dto.BlockDto;
import com.jjambbong.note.dto.BlockPageDto;
import com.jjambbong.note.entity.Block;
import com.jjambbong.note.entity.BlockPage;
import com.jjambbong.note.entity.BlockText;
import com.jjambbong.note.mapper.BlockMapper;
import com.jjambbong.note.service.BlockPageService;
import com.jjambbong.note.service.BlockPageWService;
import com.jjambbong.note.service.BlockTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlockPageWServiceImpl implements BlockPageWService {

    private static ConcurrentHashMap<String, BlockDto> map = new ConcurrentHashMap<>();
    static Date lastSavedTime = new Date();

    @Autowired
    BlockMapper blockMapper;

    @Autowired
    BlockPageService blockPageService;
    @Autowired
    BlockTextService blockTextService;

    @Override
    public void transferBlockToMap(BlockDto blockDto) {
        // TODO: 여기서 line 22에 new ConcurrentHashMap<>()을 해주지 않으면 nullPointException이 뜸 - 사유 확인 필요
        map.put(blockDto.getId(), blockDto);
        Date currentTime = new Date();
        if ((currentTime.getTime() - lastSavedTime.getTime())/1000 > 5) {
            saveBlock(currentTime);
        }
    }

    // TODO: 현재는 새로운 transferBlockToMap 함수가 호출됐을때만 호출되지만, 그렇지 않은 상황에서도 주기적으로 호출될수 있도록 개선 필요
    public synchronized void saveBlock(Date currentTime) {
        System.out.println(currentTime.getTime() - lastSavedTime.getTime());
        if ((currentTime.getTime() - lastSavedTime.getTime())/1000 > 10) {
            System.out.println("map = " + map);
            // 배치 돌리는 부분
            for(String blockId : map.keySet()){
                // API
                if(blockId.contains("page")){
                    BlockPage blockPage = blockMapper.BlockDtoToBlockPage(map.remove(blockId));
                    blockPageService.updateBlockPage(blockPage, blockId);
                } else if(blockId.contains("text")){
                    BlockText blockText = blockMapper.BlockDtoToBlockText(map.remove(blockId));
                    blockTextService.updateBlockText(blockText, blockId);
                }
            }
            lastSavedTime = new Date();
        }
    }
}