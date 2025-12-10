package com.ssafy.backend.notification.service;

import com.ssafy.backend.notification.model.Notification;
import com.ssafy.backend.notification.model.NotificationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LongPollingManager {

    //유저마다 따로 Long Polling 을 관리하기 위해서 (A user 알림은 A에게만 !)
    // 근데 ??? A 유저도 여러 Long Polling이 있을 수 있으니까.. list
    // 나중에(60초 뒤) 보낼 알림 여러개의 list
    private final Map<Long, List<DeferredResult<ResponseEntity<List<NotificationResponseDto>>>>> waiters =
            new ConcurrentHashMap<>();

    // 1. Long Polling 대기 등록
    // Controller 에서 들어온 새 Long Polling 요청의 DeferredResult를 이 메서드로 등록
    public void addWaiter(
            Long userId,
            // 유저별로 Long Polling 요청들을 저장
            DeferredResult<ResponseEntity<List<NotificationResponseDto>>> deferredResult
    ){
        // 1) userId에 해당하는 기존 waiters 리스트를 가져오거나, 없으면 새로 만들기
        waiters.compute(userId, (id, list) -> {
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(deferredResult);
            return list;
        });

        // 2) DeferredResult 가 완료되면 자동으로 waiters 에서 제거
        //응답 보냈을 때
        deferredResult.onCompletion(() -> removeWaiter(userId, deferredResult));
        //타임아웃
        deferredResult.onTimeout(() -> removeWaiter(userId, deferredResult));
        //에러
        deferredResult.onError((Throwable t) -> removeWaiter(userId, deferredResult));
    }

    // 2. 대기 중인 Long Polling 요청 제거
    private void removeWaiter(
            Long userId,
            DeferredResult<ResponseEntity<List<NotificationResponseDto>>> deferredResult
    ){
        List<DeferredResult<ResponseEntity<List<NotificationResponseDto>>>> userWaiters = waiters.get(userId);
        //userId는 Long Polling 없음
        if(userWaiters == null){
            return;
        }

        userWaiters.remove(deferredResult);

        //userId의 Long Polling list는 있는데 값이 없음. = Long Polling 보냈었고, 요청 완료되서 제거된 상태
        if(userWaiters.isEmpty()){
            waiters.remove(userId);
        }
    }

    // 3. 특정 유저에게 새 알림이 생겼을 때 호출
    public void notifyUser(Long userId, List<NotificationResponseDto> notifications){
        if(notifications == null || notifications.isEmpty()){
            return;
        }

        //유저의 DeferredResult 목록 가져오기 + 응답 + Map 제거
        List<DeferredResult<ResponseEntity<List<NotificationResponseDto>>>> userWaiters = waiters.remove(userId);

        if(userWaiters == null || userWaiters.isEmpty()){
            //대기중인 요청 없음
            return;
        }

        for(DeferredResult<ResponseEntity<List<NotificationResponseDto>>> waiter : userWaiters) {
            try{
                //200 ok + 알림 리스트
                waiter.setResult(ResponseEntity.ok(notifications));
            }catch(IllegalStateException e){
                //이미 응답이 나갔거나, 타임아웃 된 DeferredResult -> 무시
            }
        }
    }

}
