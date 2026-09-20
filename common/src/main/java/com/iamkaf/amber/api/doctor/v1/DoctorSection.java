//? if >=1.21.11 {
package com.iamkaf.amber.api.doctor.v1;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Entries for one invocation. Keys must be unique within the contributor. */
public final class DoctorSection {
    private static final int MAX_ENTRIES = 64;
    private final Set<String> keys = new HashSet<>();
    private final List<Entry> entries = new ArrayList<>();

    public void information(String key, Component label, Component value) {
        add(new Entry(key, label, Optional.empty(), value, Optional.empty()));
    }

    public void check(String key, Component label, DoctorStatus status, Component explanation) {
        check(key, label, status, explanation, Optional.empty());
    }

    public void check(String key, Component label, DoctorStatus status, Component explanation,
                      Optional<Component> remedy) {
        add(new Entry(key, label, Optional.of(status), explanation, remedy));
    }

    private void add(Entry entry) {
        if (entries.size() >= MAX_ENTRIES) {
            throw new IllegalStateException("A Doctor section may contain at most " + MAX_ENTRIES + " entries");
        }
        if (entry.key().isEmpty() || entry.key().length() > 128 || !keys.add(entry.key())) {
            throw new IllegalArgumentException("Invalid or duplicate Doctor entry key: " + entry.key());
        }
        entries.add(entry);
    }

    public List<Entry> entries() {
        return List.copyOf(entries);
    }

    public DoctorStatus status() {
        Optional<DoctorStatus> result = Optional.empty();
        for (Entry entry : entries) {
            if (entry.status().isPresent()) {
                DoctorStatus status = entry.status().get();
                result = Optional.of(result.map(previous -> previous.combine(status)).orElse(status));
            }
        }
        return result.orElse(DoctorStatus.UNKNOWN);
    }

    public record Entry(String key, Component label, Optional<DoctorStatus> status,
                        Component explanation, Optional<Component> remedy) {
        public Entry {
            Objects.requireNonNull(key);
            Objects.requireNonNull(label);
            Objects.requireNonNull(status);
            Objects.requireNonNull(explanation);
            Objects.requireNonNull(remedy);
        }
    }
}
//?}
