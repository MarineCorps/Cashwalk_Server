package com.example.cashwalk.service;

import com.example.cashwalk.dto.*;
import com.example.cashwalk.entity.RunningRecord;
import com.example.cashwalk.entity.RunningRecordPath;
import com.example.cashwalk.entity.User;
import com.example.cashwalk.repository.RunningRecordPathRepository;
import com.example.cashwalk.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunningRecordService {

    private final RunningRecordRepository runningRecordRepository;
    private final RunningRecordPathRepository runningRecordPathRepository;

    // 러닝 기록 저장
    public void saveRunningRecord(User user, RunningRecordRequestDto dto) {
        RunningRecord record = RunningRecord.builder()
                .user(user)
                .distance(dto.getDistance())
                .duration(Duration.ofSeconds(dto.getDuration()))
                .calories(dto.getCalories())
                .pace(dto.getPace())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .isDistanceMode(dto.isDistanceMode())
                .isUnlimited(dto.isUnlimited())
                .diaryLevel(dto.getDiaryLevel())
                .diaryMemo(dto.getDiaryMemo())
                .build();

        runningRecordRepository.save(record);

        List<RunningRecordPath> pathList = new ArrayList<>();
        List<LatLngDto> pathDtoList = dto.getPath();

        for (int i = 0; i < pathDtoList.size(); i++) {
            LatLngDto latLng = pathDtoList.get(i);

            RunningRecordPath path = RunningRecordPath.builder()
                    .runningRecord(record)
                    .latitude(latLng.getLat())
                    .longitude(latLng.getLng())
                    .pointOrder(i)
                    .build();

            pathList.add(path);
        }

        runningRecordPathRepository.saveAll(pathList);
    }

    // 러닝 기록 전체 조회 (카드 리스트용)
    public List<RunningRecordResponseDto> getMyRunningRecords(User user) {
        List<RunningRecord> records = runningRecordRepository.findByUser(user);

        return records.stream().map(record -> {
            RunningRecordResponseDto dto = new RunningRecordResponseDto();
            dto.setId(record.getId());
            dto.setDistance(record.getDistance());
            dto.setDuration(record.getDuration().getSeconds());
            dto.setPace(record.getPace());
            dto.setStartTime(record.getStartTime());
            dto.setEndTime(record.getEndTime());
            return dto;
        }).collect(Collectors.toList());
    }

    // 러닝 기록 상세 조회
    public RunningRecordDetailDto getRunningRecordById(User user, Long recordId) {
        RunningRecord record = runningRecordRepository.findByIdAndUser(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 러닝 기록을 찾을 수 없습니다."));

        RunningRecordDetailDto dto = new RunningRecordDetailDto();
        dto.setId(record.getId());
        dto.setDistance(record.getDistance());
        dto.setDuration(record.getDuration().getSeconds());
        dto.setCalories(record.getCalories());
        dto.setPace(record.getPace());
        dto.setStartTime(record.getStartTime());
        dto.setEndTime(record.getEndTime());
        dto.setDiaryLevel(record.getDiaryLevel());
        dto.setDiaryMemo(record.getDiaryMemo());

        // path를 DTO로 변환
        List<LatLngDto> pathDto = record.getPathList().stream()
                .sorted((a, b) -> Integer.compare(a.getPointOrder(), b.getPointOrder()))
                .map(p -> {
                    LatLngDto latLng = new LatLngDto();
                    latLng.setLat(p.getLatitude());
                    latLng.setLng(p.getLongitude());
                    return latLng;
                })
                .collect(Collectors.toList());

        dto.setPath(pathDto);

        return dto;
    }

    public void updateDiary(User user, Long recordId, RunningDiaryUpdateDto dto) {
        RunningRecord record = runningRecordRepository.findByIdAndUser(recordId, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 러닝 기록이 없습니다."));

        record.setDiaryLevel(dto.getDiaryLevel());
        record.setDiaryMemo(dto.getDiaryMemo());

        runningRecordRepository.save(record);
    }

    public void deleteRunningRecord(Long userId, Long recordId) {
        RunningRecord record = runningRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("러닝 기록이 존재하지 않습니다."));

        if (!record.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 기록을 삭제할 권한이 없습니다.");
        }

        runningRecordRepository.delete(record);
    }


}
