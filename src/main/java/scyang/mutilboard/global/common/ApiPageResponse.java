package scyang.mutilboard.global.common;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ApiPageResponse<T> extends ResponseEntity<ApiPageResponse.PagePayload<T>> {

    @Getter
    public static class PagePayload<T>{
        private final boolean success = true;
        private final String message;
        private final List<T> data;
        private final int pageNumber;
        private final int pageSize;
        private final long totlaElements;
        private final int totalPages;
        private final boolean hasNext;
        //private final boolean hasPrevious;


        public PagePayload(Page<T> page, String message) {
            this.data = page.getContent();
            this.pageNumber = page.getNumber();
            this.pageSize = page.getSize();
            this.totlaElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.hasNext = page.hasNext();
            this.message = message;
        }
    }

    public ApiPageResponse(Page<T> page, String message) {
        super(new PagePayload<>(page, message), HttpStatus.OK);
    }
}
