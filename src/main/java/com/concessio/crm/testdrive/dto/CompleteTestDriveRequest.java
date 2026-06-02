package com.concessio.crm.testdrive.dto;

public class CompleteTestDriveRequest {
    private String notes;
    private Integer kmAfter;

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getKmAfter() { return kmAfter; }
    public void setKmAfter(Integer kmAfter) { this.kmAfter = kmAfter; }
}
