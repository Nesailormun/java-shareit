package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;
    private final Long itemId = 2L;

    private final ItemDto itemDto = new ItemDto(
            itemId, "Дрель", "Простая дрель", true, userId, null
    );

    private final CommentDto commentDto = new CommentDto(
            1L, "Отличный инструмент!", "Иван", LocalDateTime.now()
    );

    private final ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(
            itemId, "Дрель", "Простая дрель", true, userId, null,
            List.of(commentDto), null, null
    );

    private final ItemDto updatedItemDto = new ItemDto(
            itemId, "Отбойный молоток", "Мощный инструмент для демонтажа", false, userId, null
    );

    @Test
    void getUsersItemsTest() throws Exception {
        Mockito.when(itemService.getUsersItems(userId))
                .thenReturn(List.of(itemWithBookingsDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].comments[0].text").value("Отличный инструмент!"));
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito.when(itemService.getItemById(userId, itemId))
                .thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.comments[0].authorName").value("Иван"));
    }

    @Test
    void getItemsByTextTest() throws Exception {
        String text = "дрель";

        Mockito.when(itemService.getItemsByText(userId, text))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void createItemTest() throws Exception {
        Mockito.when(itemService.addNewItem(eq(userId), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.description").value("Простая дрель"));
    }

    @Test
    void updateItemTest() throws Exception {
        Mockito.when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class)))
                .thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Отбойный молоток"))
                .andExpect(jsonPath("$.description").value("Мощный инструмент для демонтажа"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        Mockito.verify(itemService).deleteItem(userId, itemId);
    }

    @Test
    void addCommentTest() throws Exception {
        Mockito.when(itemService.addComment(eq(userId), eq(itemId), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отличный инструмент!"))
                .andExpect(jsonPath("$.authorName").value("Иван"));
    }
}
