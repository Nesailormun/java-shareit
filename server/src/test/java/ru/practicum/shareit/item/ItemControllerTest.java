package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)

public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mvc;

    final Long userId = 1L;
    final Long itemId = 10L;

    final ItemDto itemDto = new ItemDto();
    {
        itemDto.setId(itemId);
        itemDto.setName("Молоток");
        itemDto.setDescription("Строительный молоток");
        itemDto.setAvailable(true);
        itemDto.setRequestId(5L);
    }

    final ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto();
    {
        itemWithBookingsDto.setId(itemDto.getId());
        itemWithBookingsDto.setName(itemDto.getName());
        itemWithBookingsDto.setDescription(itemDto.getDescription());
        itemWithBookingsDto.setAvailable(itemDto.getAvailable());
        itemWithBookingsDto.setRequestId(itemDto.getRequestId());
        itemWithBookingsDto.setComments(Collections.emptyList());
    }

    final CommentDto commentDto = new CommentDto();
    {
        commentDto.setId(1L);
        commentDto.setText("Очень полезный инструмент");
        commentDto.setAuthorName("Иван");
        commentDto.setCreated(LocalDateTime.of(2025, 6, 1, 12, 0));
    }

    @Test
    void getUsersItems() throws Exception {
        when(itemService.getUsersItems(userId)).thenReturn(List.of(itemWithBookingsDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemWithBookingsDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemWithBookingsDto.getName()));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(userId, itemId)).thenReturn(itemWithBookingsDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemWithBookingsDto.getId()))
                .andExpect(jsonPath("$.name").value(itemWithBookingsDto.getName()));
    }

    @Test
    void getItemsByText() throws Exception {
        when(itemService.getItemsByText(userId, "молоток")).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "молоток"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.addNewItem(userId, itemDto)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updatedDto = new ItemDto();
        updatedDto.setId(itemDto.getId());
        updatedDto.setName("Обновленный молоток");
        updatedDto.setDescription(itemDto.getDescription());
        updatedDto.setAvailable(itemDto.getAvailable());
        updatedDto.setRequestId(itemDto.getRequestId());

        when(itemService.updateItem(userId, itemId, itemDto)).thenReturn(updatedDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный молоток"));
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).deleteItem(userId, itemId);

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(userId, itemId, commentDto)).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}
