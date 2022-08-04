package com.points.PointsManagementProject.job.readerCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.function.Function;

public class ReverseJpaPagingItemReaderBuilder<T> {

    private String name;
    private Function<Pageable, Page<T>> query;
    private int pageSize;
    private Sort sort;

    /**
     * ReverseJpaPagingItemReaderBuilder.name(String);
     */
    public ReverseJpaPagingItemReaderBuilder<T> name(String name) {
        this.name = name;

        return this;
    }

    /**
     * ReverseJpaPagingItemReaderBuilder.query(Function<Pageable, Page<T>>);
     */
    public ReverseJpaPagingItemReaderBuilder<T> query(Function<Pageable, Page<T>> query) {
        this.query = query;

        return this;
    }

    /**
     * ReverseJpaPagingItemReaderBuilder.pageSize(int);
     */
    public ReverseJpaPagingItemReaderBuilder<T> pageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;

    }

    /**
     * ReverseJpaPagingItemReaderBuilder.sort(Sort);
     */
    public ReverseJpaPagingItemReaderBuilder<T> sort(Sort sort) {
        this.sort = sort;

        return this;
    }


    /**
     * ReverseJpaPagingItemReaderBuilder.build();
     */
    public ReverseJpaPagingItemReader<T> build() {
        /**
         * Check Parameters are not null
         */
        Assert.notNull(this.query, "Query is required.");
        Assert.notNull(this.name, "Name is required.");

        /**
         * Reader 생성
         */
        ReverseJpaPagingItemReader<T> reader = new ReverseJpaPagingItemReader<>();

        reader.setName(this.name);
        reader.setQuery(this.query);
        reader.setPageSize(this.pageSize);
        reader.setSort(this.sort);

        return reader;
    }
}
