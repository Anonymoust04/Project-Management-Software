
package com.example.application;

import com.example.application.views.AverageTimeSpentView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TimeSpentService {

    public Optional<List<AverageTimeSpentView.UserTimeSpent>> getAverageTimeSpent(LocalDate startDate, LocalDate endDate) {
        return Optional.empty();
    }
}