package com.fc.housebatch.core.job.lawd;

import com.fc.housebatch.core.entity.Lawd;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class LawdFieldSetMapper implements FieldSetMapper<Lawd> {

    public static final String LAWD_CD = "lawdCd";
    public static final String LAWD_DONG = "lawdDong";
    public static final String EXIST = "exist";

    private static final String EXIST_TRUE = "존재";

    @Override
    public Lawd mapFieldSet(FieldSet fieldSet) throws BindException {
        /**
         * FlatFileItemReader 에서 데이터를 delimiter 로 구분한 후, 각 column 을 순서대로 원하는 이름과 매핑해 줬음 ( .delimiter('\t'), .names("lawdCd", "lawdDong", "exists"))
         * 매핑한 이름을 그대로 데이터를 읽을때 filedSet 에서 사용할 수 있음
         */
        String lawdCd = fieldSet.readString(LAWD_CD);
        String lawdDong = fieldSet.readString(LAWD_DONG);
        boolean exist = fieldSet.readBoolean(EXIST, EXIST_TRUE); // 파일에서 exist 로 매핑된 데이터 중(폐지여부)에서, 값이 '존재'이면 true, '패지'이면 false 반환

        return new Lawd(lawdCd, lawdDong, exist);
    }
}
