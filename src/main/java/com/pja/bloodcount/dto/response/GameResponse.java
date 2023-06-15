package com.pja.bloodcount.dto.response;

import com.pja.bloodcount.model.Case;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {

    private Long id;
    private Date startTime;
    private Date endTime;
    private Date estimatedEndTime;
    private int testDuration;
    private Status status;
    private PatientResponse patient;
    private CaseOfGameResponse gameCase;
}
