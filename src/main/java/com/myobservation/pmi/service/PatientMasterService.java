package com.myobservation.pmi.service;

import com.myobservation.auth.dto.UserRequest;
import com.myobservation.auth.dto.UserResponse;
import com.myobservation.pmi.model.dto.PMIRequest;
import com.myobservation.pmi.model.dto.PMIResponse;

import java.util.List;
import java.util.Optional;

public interface PatientMasterService {

    List<PMIResponse> getAllPatients();

    Optional<PMIResponse> getPatientById(Long patientId);

    PMIResponse createPatient(PMIRequest patientRequest) throws PatientMapperAlreadyExists;

    Optional<PMIResponse> updatePatient(Long patientId, PMIRequest patientRequest);

    boolean deletePatient(Long patientId);
}