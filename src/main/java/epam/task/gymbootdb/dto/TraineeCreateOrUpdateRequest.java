package epam.task.gymbootdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeCreateOrUpdateRequest {
    //TODO validation
    private long id;
    private UserCreateOrUpdateRequest user;
    private LocalDate birthday;
    private String address;
}