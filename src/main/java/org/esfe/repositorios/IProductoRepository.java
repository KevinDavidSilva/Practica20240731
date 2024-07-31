package org.esfe.repositorios;

import org.esfe.models.ProductoKDSB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductoRepository extends JpaRepository<ProductoKDSB, Integer> {
}
