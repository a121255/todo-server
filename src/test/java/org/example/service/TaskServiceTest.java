package org.example.service;

import org.example.constants.TaskStatus;
import org.example.persist.TaskRepository;
import org.example.persist.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //아까 의존성 추가했고 이걸로 활성화
class TaskServiceTest {

    // mock 객체 생성 - 해당 객체 이용해서 실체 객체 대체하여 mock 객체 검증 가능,
    // 실제 객체와 비슷한 동작 수행하지만 실제와는 다르게 미리 정해둔 동작을 한다.
    // 이렇게 하면 해당 클래스나 라이브러리와같은 의존성을 완전히 제거할 수 있게 된다.
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks //mock 인스턴스 생성하며 모든 의존성을 주입해줌
    private TaskService taskService;

    @Test
    @DisplayName("할일 추가 기능 테스트") //어떤 테스트인지 설명하는 주석
    void add() {
        var title="test";
        var description="test description";
        var dueDate = LocalDate.now();

        // 실제 DB에 연결하지 않음
        // 어떻게 동작할지 정의해야함
        when(taskRepository.save(any(TaskEntity.class))) // any 매개변수 지정하지 않고 모든 값을 허용
                .thenAnswer(invocation -> {
                    var e = (TaskEntity)invocation.getArgument(0);
                    e.setId(1L);
                    e.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    e.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    return e;
                });

        var actual = taskService.add(title, description, dueDate);

        verify(taskRepository, times(1)).save(any()); // 딱 한번 호출되었는지 검증
        assertEquals(1L, actual.getId());
        assertEquals(title, actual.getTitle());
        assertEquals(description, actual.getDescription());
        assertEquals(dueDate.toString(), actual.getDueDate());
        assertEquals(TaskStatus.IN_PROGRESS, actual.getStatus()); //TO_DO여야 해서 fail
        assertNotNull(actual.getCreatedAt());
        assertNotNull(actual.getUpdatedAt());

        // 실제 DB에 IO 일어나지 않음 -> 의존성 없음, DB 동작 외 비즈니스 로직 정상 동작하는지 테스트 완료함
    }
}