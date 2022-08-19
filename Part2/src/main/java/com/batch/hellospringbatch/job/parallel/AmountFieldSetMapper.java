package com.batch.hellospringbatch.job.parallel;

import com.batch.hellospringbatch.core.domain.dto.AmountDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class AmountFieldSetMapper implements FieldSetMapper {
    @Override
    public Object mapFieldSet(FieldSet fieldSet) throws BindException {
        int index = fieldSet.readInt(0);
        String name = fieldSet.readString(1);
        int amount = fieldSet.readInt(2);

        return new AmountDto(index, name, amount);
    }
}
