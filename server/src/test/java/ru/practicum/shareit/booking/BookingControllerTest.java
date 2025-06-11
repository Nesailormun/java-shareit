package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBookingTest() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);

        Mockito.when(bookingService.createBooking(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).createBooking(eq(123L), any(BookingRequestDto.class));
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);

        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 123)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).approveBooking(123L, 1L, true);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);

        Mockito.when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).getBookingById(123L, 1L);
    }

    @Test
    void getUserBookingsTest() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        Mockito.when(bookingService.getBookingsByUser(anyLong(), anyString()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService).getBookingsByUser(123L, "ALL");
    }

    @Test
    void getOwnerBookingsTest() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        Mockito.when(bookingService.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService).getOwnerBookings(123L, "ALL");
    }
}
