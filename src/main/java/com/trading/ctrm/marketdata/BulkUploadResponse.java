package com.trading.ctrm.marketdata;

public class BulkUploadResponse {
    
    private int total;
    private int created;
    private int updated;
    private int errors;
    private String message;

    public BulkUploadResponse(int total, int created, int updated, int errors) {
        this.total = total;
        this.created = created;
        this.updated = updated;
        this.errors = errors;
        this.message = String.format(
            "Processed %d records: %d created, %d updated, %d errors",
            total, created, updated, errors
        );
    }

    // Getters
    public int getTotal() {
        return total;
    }

    public int getCreated() {
        return created;
    }

    public int getUpdated() {
        return updated;
    }

    public int getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }
}
