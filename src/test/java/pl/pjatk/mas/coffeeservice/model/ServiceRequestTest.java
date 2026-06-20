package pl.pjatk.mas.coffeeservice.model;

import org.junit.jupiter.api.Test;
import pl.pjatk.mas.coffeeservice.model.enums.RepairResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceRequestTest {

    @Test
    void attachDiagnosisShouldAssignDiagnosisToRequest() {
        ServiceRequest request = new ServiceRequest(
                LocalDateTime.now(),
                "Ekspres nie spienia mleka."
        );

        Diagnosis diagnosis = new Diagnosis(
                LocalDateTime.now(),
                "Uszkodzony układ spieniania.",
                BigDecimal.valueOf(150),
                true
        );

        request.attachDiagnosis(diagnosis);

        assertSame(diagnosis, request.getDiagnosis());
        assertSame(request, diagnosis.getServiceRequest());
    }

    @Test
    void attachDiagnosisShouldRejectSecondDiagnosis() {
        ServiceRequest request = new ServiceRequest(
                LocalDateTime.now(),
                "Ekspres nie parzy kawy."
        );

        Diagnosis firstDiagnosis = new Diagnosis(
                LocalDateTime.now(),
                "Awaria pompy.",
                BigDecimal.valueOf(200),
                true
        );

        Diagnosis secondDiagnosis = new Diagnosis(
                LocalDateTime.now(),
                "Inna diagnoza.",
                BigDecimal.valueOf(300),
                true
        );

        request.attachDiagnosis(firstDiagnosis);

        assertThrows(IllegalStateException.class, () -> request.attachDiagnosis(secondDiagnosis));
    }

    @Test
    void serviceReportShouldCalculateFinalCostFromRepairs() {
        ServiceRequest request = new ServiceRequest(
                LocalDateTime.now(),
                "Ekspres wymaga naprawy."
        );

        Repair repair1 = new Repair(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                RepairResult.SUCCESSFUL,
                "Czyszczenie bloku zaparzającego.",
                BigDecimal.valueOf(100)
        );

        Repair repair2 = new Repair(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                RepairResult.SUCCESSFUL,
                "Wymiana uszczelki.",
                BigDecimal.valueOf(50)
        );

        request.addRepair(repair1);
        request.addRepair(repair2);

        ServiceReport report = new ServiceReport(
                LocalDateTime.now(),
                "Wykonano naprawy.",
                "Zalecany przegląd za 12 miesięcy."
        );

        request.attachServiceReport(report);

        assertEquals(0, BigDecimal.valueOf(150).compareTo(report.calculateFinalCost()));
    }
}