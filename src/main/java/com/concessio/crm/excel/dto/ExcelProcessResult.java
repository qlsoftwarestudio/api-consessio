package com.concessio.crm.excel.dto;

import java.util.ArrayList;
import java.util.List;

public class ExcelProcessResult {
    private int totalRows;
    private int successRows;
    private int errorRows;
    private int duplicateRows;
    private List<ExcelRowError> errors = new ArrayList<>();
    private List<Long> createdLeadIds = new ArrayList<>();

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getSuccessRows() { return successRows; }
    public void setSuccessRows(int successRows) { this.successRows = successRows; }

    public int getErrorRows() { return errorRows; }
    public void setErrorRows(int errorRows) { this.errorRows = errorRows; }

    public int getDuplicateRows() { return duplicateRows; }
    public void setDuplicateRows(int duplicateRows) { this.duplicateRows = duplicateRows; }

    public List<ExcelRowError> getErrors() { return errors; }
    public void setErrors(List<ExcelRowError> errors) { this.errors = errors; }

    public List<Long> getCreatedLeadIds() { return createdLeadIds; }
    public void setCreatedLeadIds(List<Long> createdLeadIds) { this.createdLeadIds = createdLeadIds; }

    public void addError(int rowNumber, String field, String message) {
        this.errors.add(new ExcelRowError(rowNumber, field, message));
    }

    public static class ExcelRowError {
        private int rowNumber;
        private String field;
        private String message;

        public ExcelRowError(int rowNumber, String field, String message) {
            this.rowNumber = rowNumber;
            this.field = field;
            this.message = message;
        }

        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
