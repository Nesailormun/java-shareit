package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeDeserialize() throws Exception {
        ItemRequestDto requestDto = init();

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"id\":100");
        assertThat(json).contains("\"description\":\"Ищу инструменты\"");
        assertThat(json).contains("\"requester\":3");
        assertThat(json).contains("\"created\":\"2025-06-10T12:00:00\"");
        assertThat(json).contains("\"items\"");

        ItemRequestDto deserialized = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(deserialized.getId()).isEqualTo(100L);
        assertThat(deserialized.getDescription()).isEqualTo("Ищу инструменты");
        assertThat(deserialized.getRequester()).isEqualTo(3L);
        assertThat(deserialized.getCreated()).isEqualTo(LocalDateTime.of(2025, 6, 10, 12, 0, 0));
        assertThat(deserialized.getItems()).hasSize(1);

        ItemDto deserializedItem = deserialized.getItems().getFirst();
        assertThat(deserializedItem.getId()).isEqualTo(10L);
        assertThat(deserializedItem.getName()).isEqualTo("Отвертка");
        assertThat(deserializedItem.getDescription()).isEqualTo("Крестовая отвертка");
        assertThat(deserializedItem.getAvailable()).isTrue();
        assertThat(deserializedItem.getOwnerId()).isEqualTo(5L);
    }


    private static ItemRequestDto init() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Отвертка");
        itemDto.setDescription("Крестовая отвертка");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(5L);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(100L);
        requestDto.setDescription("Ищу инструменты");
        requestDto.setRequester(3L);
        requestDto.setCreated(LocalDateTime.of(2025, 6, 10, 12, 0, 0));
        requestDto.setItems(List.of(itemDto));
        return requestDto;
    }
}
