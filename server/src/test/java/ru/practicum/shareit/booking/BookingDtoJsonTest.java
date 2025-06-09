package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeDeserialize() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2025, 6, 10, 10, 0));
        dto.setEnd(LocalDateTime.of(2025, 6, 15, 10, 0));

        ItemShortDto item = new ItemShortDto();
        item.setId(100L);
        item.setName("Test Item");
        dto.setItem(item);

        UserShortDto user = new UserShortDto();
        user.setId(200L);
        user.setName("Test User");
        dto.setBooker(user);

        dto.setStatus("APPROVED");

        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2025-06-10T10:00:00\"");
        assertThat(json).contains("\"end\":\"2025-06-15T10:00:00\"");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"id\":100"); // из item
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"id\":200"); // из booker
        assertThat(json).contains("\"name\":\"Test User\"");

        BookingDto result = objectMapper.readValue(json, BookingDto.class);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(dto.getStart());
        assertThat(result.getEnd()).isEqualTo(dto.getEnd());
        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(100L);
        assertThat(result.getBooker()).isNotNull();
        assertThat(result.getBooker().getName()).isEqualTo("Test User");
    }
}
