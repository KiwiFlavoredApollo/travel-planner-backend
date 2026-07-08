package com.travelplanner.demo.travelplan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.travelplanner.demo.destination.entity.DestinationEntity;

@Entity
@Table(name = "Travel_Plan_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USER_ID", length = 20, nullable = false)
    private String userId;

    @Column(name = "AREA", length = 20, nullable = false)
    private String area;

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DestinationEntity> destinations = new ArrayList<>();

    public void addDestination(DestinationEntity destination) {
        destinations.add(destination);
        destination.setTravelPlan(this);
    }
}