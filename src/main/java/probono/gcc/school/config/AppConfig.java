package probono.gcc.school.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import probono.gcc.school.model.dto.TeacherCreateRequestDto;
import probono.gcc.school.model.entity.Teacher;

@Configuration
public class AppConfig {


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // 필요한 매핑 설정 추가
        modelMapper.addMappings(new PropertyMap<TeacherCreateRequestDto, Teacher>() {
            @Override
            protected void configure() {
                // 여기서 필요한 매핑 설정을 추가합니다.
                map().setName(source.getName());
                map().setLoginId(source.getLoginId());
                map().setLoginPw(source.getLoginPw());
            }
        });

        return modelMapper;
    }
}
