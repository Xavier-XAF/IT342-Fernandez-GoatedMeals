package edu.cit.fernandez.goatedmeals.scheduling;

import edu.cit.fernandez.goatedmeals.auth.User;
import edu.cit.fernandez.goatedmeals.auth.UserRepository;
import edu.cit.fernandez.goatedmeals.billing.Subscription;
import edu.cit.fernandez.goatedmeals.billing.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MealScheduleRepository mealScheduleRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ScheduleController scheduleController;

    private User testUser;
    private MealSchedule testSchedule;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("xavier@test.com");

        testSchedule = new MealSchedule();
        testSchedule.setId(100L);
        testSchedule.setUser(testUser);

        testSubscription = new Subscription();
        testSubscription.setAvailableCredits(5);
    }

    @Test
    void testCancelSchedule_SuccessAndRefundsCredit() {
        // --- ARRANGE ---
        when(authentication.getName()).thenReturn("xavier@test.com");
        when(userRepository.findByEmail("xavier@test.com")).thenReturn(Optional.of(testUser));
        when(mealScheduleRepository.findById(100L)).thenReturn(Optional.of(testSchedule));
        when(subscriptionRepository.findByUserIdAndStatus(1L, "ACTIVE")).thenReturn(Optional.of(testSubscription));

        // --- ACT ---
        ResponseEntity<?> response = scheduleController.cancelSchedule(authentication, 100L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue(responseBody.get("message").toString().contains("successfully cancelled"));

        assertEquals(6, testSubscription.getAvailableCredits());

        verify(mealScheduleRepository, times(1)).delete(testSchedule);
        verify(subscriptionRepository, times(1)).save(testSubscription);
    }
}