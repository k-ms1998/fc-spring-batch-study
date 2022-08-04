package com.points.PointsManagementProject.job.readerCustom;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class ReverseJpaPagingItemReader<T> extends ItemStreamSupport implements ItemReader<T> {
    private static final int DEFAULT_PAGE_SIZE = 100;

    private int page = 0;
    private int totalPage = 0;
    private List<T> data = new ArrayList<>();

    private int pageSize = DEFAULT_PAGE_SIZE;
    private Function<Pageable, Page<T>> query;
    private Sort sort = Sort.unsorted();

    /**
     * 1. Step 을 실행하기 전에 읽을 데이터가 총 몊 페이지인지 가져오기
     * 2. 뒤에서 부터 데이터를 읽을것이기 때문에, 처음에 page 를 마지막 page 로 설정 해줌
     *  -> if) totalPage == 3 => 총 3개의 페이지 존재 => page 0 && page 1 && page 2 => 배열처럼 인덱스 0 부터 시작
     */
    @BeforeStep
    public void beforeStep() {
        this.totalPage = getTargetData(0).getTotalPages();
        this.page = this.totalPage - 1;
        log.info("{ totalPage } = " + totalPage);
    }

    /**
     * 데이터를 읽기 전에
     * 1. page 가 0 보다 작으면, 마지막 페이지부터 페이지 1까지 모두 읽은 것 -> 종료
     * 2. data 를 현재 페에지의 데이터들로 채워주기
     */
    @BeforeRead
    public void beforeRead() {
        log.info("{ Page Number } = " + page);

        if (page < 0) {
            return;
        }

        if (data.isEmpty()) {
            data = getTargetData(page).getContent().stream().collect(Collectors.toList());
        }
    }

    /**
     * 현재 페이지의 데이터 모두 읽은 후 -> page 의 값을 감소해서 다음 페이지를 읽도록 설정
     * !! (마지막 페이지부터 데이터를 읽기 때문에, 현재 페이지 다음 순서로 읽을 페이지는 현재 페이지보다 하나 작은 페이지 이다) !!
     */
    @AfterRead
    public void afterRead() {
        if (data.isEmpty()) {
            page--;
        }
    }

    /**
     * 현재 페이지의 모든 데이터 읽기 (data 에 현재 페이지의 데이터가 들어 있음)
     * 1. data.isEmpty() == true -> 현재 데이터를 모두 읽은 상태 => null 반환
     * 2. data.isEmpty() == false -> 현재 데이터의 가장 마지막 데이터를 삭제 && 반환
     */
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return data.isEmpty() ? null : data.remove(data.size() - 1);
    }

    private Page<T> getTargetData(int currentPage) {
        return query == null ? Page.empty() : query.apply(PageRequest.of(currentPage, pageSize, sort));
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
    }

    public void setQuery(Function<Pageable, Page<T>> query) {
        this.query = query;
    }

    /**
     * pagination 을 뒤에서 부터 하기 때문에 sort direction 을 모두 reverse 하기
     * @param sort
     */
    public void setSort(Sort sort) {
        if(!Objects.isNull(sort)){ // sort != null
            Iterator<Sort.Order> iterator = sort.iterator();
            List<Sort.Order> reverseOrder = new LinkedList<>();
            while (iterator.hasNext()) {
                Sort.Order prev = iterator.next();
                /**
                 * new Sort.Order(Direction direction, String property)
                 */
                reverseOrder.add(new Sort.Order(prev.getDirection().isAscending() ? Sort.Direction.DESC : Sort.Direction.ASC, prev.getProperty()));
            }

            this.sort = Sort.by(reverseOrder);
        }
    }
}
