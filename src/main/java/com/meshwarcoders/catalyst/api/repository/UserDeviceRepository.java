package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.model.UserDeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDeviceModel, Long> {
    Optional<UserDeviceModel> findByToken(String token);

    List<UserDeviceModel> findByTeacher(TeacherModel teacher);

    List<UserDeviceModel> findByStudent(StudentModel student);

    List<UserDeviceModel> findByStudentIn(List<StudentModel> students);
}
