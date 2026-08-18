package cl.duoc.bancoxyz.migracion.batch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estados_cuenta_anuales")
public class EstadoCuentaEntity {

    @Id
    private Long id;

    private Long cuentaId;
    private Integer anio;
    private Integer cantidadMovimientos;
    private Double totalMovimientos;

    public EstadoCuentaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getCantidadMovimientos() {
        return cantidadMovimientos;
    }

    public void setCantidadMovimientos(Integer cantidadMovimientos) {
        this.cantidadMovimientos = cantidadMovimientos;
    }

    public Double getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(Double totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }
}