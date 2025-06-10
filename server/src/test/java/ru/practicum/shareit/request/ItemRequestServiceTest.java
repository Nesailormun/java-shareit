package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(new User(null, "User1", "user1@example.com"));
        user2 = userRepository.save(new User(null, "User2", "user2@example.com"));

        request1 = new ItemRequest();
        request1.setDescription("Нужен молоток");
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        request1 = itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Ищу отвертку");
        request2.setRequester(user2);
        request2.setCreated(LocalDateTime.now());
        request2 = itemRequestRepository.save(request2);

        Item itemForRequest2 = new Item(0L, "Отвертка крестовая", "Отвертка для шурупов", true, user1, request2);
        itemRepository.save(itemForRequest2);
    }

    @Test
    void createRequestTest() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto created = itemRequestService.createRequest(user1.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
        assertThat(created.getRequester()).isEqualTo(user1.getId());
        assertThat(created.getCreated()).isNotNull();
    }

    @Test
    void createRequestWhenUserNotFoundThrowsTest() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Запрос");

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(999L, dto));
    }

    @Test
    void getUserRequestsTest() {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(user1.getId());

        assertThat(requests).isNotEmpty();
        assertThat(requests).allMatch(r -> r.getRequester().equals(user1.getId()));
    }

    @Test
    void getAllRequestsTest() {
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user1.getId(), 0, 10);

        assertThat(requests).isNotEmpty();
        assertThat(requests).noneMatch(r -> r.getRequester().equals(user1.getId()));

        ItemRequestDto req2 = requests.stream()
                .filter(r -> r.getId().equals(request2.getId()))
                .findFirst()
                .orElse(null);

        assertThat(req2).isNotNull();
        assertThat(req2.getItems()).isNotEmpty();
        assertThat(req2.getItems().getFirst().getName()).isEqualTo("Отвертка крестовая");
    }

    @Test
    void getRequestByIdTest() {
        ItemRequestDto dto = itemRequestService.getRequestById(user1.getId(), request2.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(request2.getId());
        assertThat(dto.getRequester()).isEqualTo(user2.getId());
        assertThat(dto.getItems()).isNotEmpty();
    }

    @Test
    void getRequestByIdWhenUserNotFoundThrowsTest() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(999L, request1.getId()));
    }

    @Test
    void getRequestByIdWhenRequestNotFoundThrowsTest() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(user1.getId(), 999L));
    }
}
