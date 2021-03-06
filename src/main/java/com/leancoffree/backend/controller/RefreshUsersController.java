package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.RefreshUsersService;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RefreshUsersController {

  private final RefreshUsersService refreshUsersService;

  public RefreshUsersController(final RefreshUsersService refreshUsersService) {
    this.refreshUsersService = refreshUsersService;
  }

  @CrossOrigin
  @PostMapping("/refresh-users")
  public ResponseEntity<SuccessOrFailureAndErrorBody> addOrDropUserEndpoint(
      @Valid @RequestBody final RefreshUsersRequest refreshUsersRequest, final Errors errors) {

    if (errors.hasErrors()) {
      return buildValidationErrorsResponse(errors);
    }
    return ResponseEntity.ok(refreshUsersService.refreshUsers(refreshUsersRequest));
  }

  @CrossOrigin
  @GetMapping("/refresh-users/{sessionId}")
  public ResponseEntity<SuccessOrFailureAndErrorBody> refreshUsersEndpoint(
      @PathVariable("sessionId") final String sessionId) {
    return ResponseEntity.ok(refreshUsersService.quickRefresh(sessionId));
  }

  private ResponseEntity<SuccessOrFailureAndErrorBody> buildValidationErrorsResponse(
      final Errors errors) {
    final List<String> errorsList = new ArrayList<>();
    for (final ObjectError error : errors.getAllErrors()) {
      errorsList.add(error.getDefaultMessage());
    }
    return ResponseEntity.ok(SessionStatusResponse.builder()
        .status(FAILURE)
        .error(String.join(", ", errorsList))
        .build());
  }
}