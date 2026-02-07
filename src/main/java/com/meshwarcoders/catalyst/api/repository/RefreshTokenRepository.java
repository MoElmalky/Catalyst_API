package com.meshwarcoders.catalyst.api.repository;


import com.meshwarcoders.catalyst.api.model.RefreshTokenModel;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenModel, Long> {
    List<RefreshTokenModel> findByFamilyId(String familyId);

    Optional<RefreshTokenModel> findByToken(String token);

    Optional<RefreshTokenModel> findByTeacher(TeacherModel teacher);

    Optional<RefreshTokenModel> findByStudent(StudentModel student);

    @Modifying
    @Query("UPDATE refresh_tokens rt SET rt.revoked = true WHERE rt.familyId = :familyId")
    int revokeFamily(@Param("familyId") String familyId);
}
