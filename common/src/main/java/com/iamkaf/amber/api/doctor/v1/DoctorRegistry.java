//? if >=1.21.11 {
package com.iamkaf.amber.api.doctor.v1;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.doctor.DoctorText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

final class DoctorRegistry<C> {
    private final TreeMap<String, Registration<C>> registrations = new TreeMap<>();

    synchronized void register(AmberModInfo mod, DoctorContributor<C> contributor) {
        Objects.requireNonNull(mod);
        Objects.requireNonNull(contributor);
        if (registrations.putIfAbsent(mod.id(), new Registration<>(mod, contributor)) != null) {
            throw new IllegalArgumentException("Duplicate Doctor contributor: " + mod.id());
        }
    }

    List<Doctor.Report> inspect(C context) {
        List<Registration<C>> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(registrations.values());
        }
        List<Doctor.Report> reports = new ArrayList<>();
        for (Registration<C> registration : snapshot) {
            DoctorSection section = new DoctorSection();
            try {
                registration.contributor().contribute(context, section);
            } catch (Exception exception) {
                Constants.LOG.error("Doctor contributor failed: {}", registration.mod().id(), exception);
                section = new DoctorSection();
                section.check("contributor_failure", DoctorText.translatable("amber.doctor.contributor"),
                        DoctorStatus.ERROR, DoctorText.translatable("amber.doctor.contributor_failed"));
            }
            reports.add(new Doctor.Report(registration.mod(), section.status(), section.entries()));
        }
        return List.copyOf(reports);
    }

    private record Registration<C>(AmberModInfo mod, DoctorContributor<C> contributor) {
    }
}
//?}
