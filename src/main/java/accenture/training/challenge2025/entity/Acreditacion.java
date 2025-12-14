package accenture.training.challenge2025.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "acreditaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Acreditacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "punto_venta_id")
    private Integer puntoVentaId;

    @Column(name = "nombre_punto_venta")
    private String nombrePuntoVenta;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    private BigDecimal importe;
}
