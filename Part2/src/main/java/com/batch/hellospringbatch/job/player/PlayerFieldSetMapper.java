package com.batch.hellospringbatch.job.player;

import com.batch.hellospringbatch.core.domain.dto.PlayerDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PlayerFieldSetMapper implements FieldSetMapper<PlayerDto> {

    /**
     * Delimiter 에 의해 구분된 데이터(fieldSet) 을 PlayerDto 로 매핑하기
     */
    @Override
    public PlayerDto mapFieldSet(FieldSet fieldSet) throws BindException {
        String id = fieldSet.readString(0); // ',' 로 구분된 데이터에서 0 번째 인덱스에 있는 값을 가져오기
        String lastName = fieldSet.readString(1); // ',' 로 구분된 데이터에서 1 번째 인덱스에 있는 값을 가져오기
        String firstName = fieldSet.readString(2); // ',' 로 구분된 데이터에서 2 번째 인덱스에 있는 값을 가져오기
        String position = fieldSet.readString(3); // ',' 로 구분된 데이터에서 3 번째 인덱스에 있는 값을 가져오기
        int birthYear = fieldSet.readInt(4); // ',' 로 구분된 데이터에서 4 번째 인덱스에 있는 값을 가져오기
        int debutYear = fieldSet.readInt(5); // ',' 로 구분된 데이터에서 5 번째 인덱스에 있는 값을 가져오기

        return new PlayerDto(id, lastName, firstName, position, birthYear, debutYear);
    }
}
