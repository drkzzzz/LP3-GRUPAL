import { Routes, Route, Navigate } from 'react-router-dom';
import { StorefrontLayout } from '../layouts/StorefrontLayout';
import { StorefrontHome } from '../pages/StorefrontHome';
import { StorefrontCatalog } from '../pages/StorefrontCatalog';
import { StorefrontProductDetail } from '../pages/StorefrontProductDetail';
import { StorefrontCart } from '../pages/StorefrontCart';
import { StorefrontCheckout } from '../pages/StorefrontCheckout';
import { StorefrontLogin } from '../pages/StorefrontLogin';
import { StorefrontRegister } from '../pages/StorefrontRegister';
import { StorefrontOrders } from '../pages/StorefrontOrders';

export const StorefrontRoutes = () => {
  return (
    <Routes>
      <Route path=":slug" element={<StorefrontLayout />}>
        <Route index element={<StorefrontHome />} />
        <Route path="catalogo" element={<StorefrontCatalog />} />
        <Route path="producto/:productoId" element={<StorefrontProductDetail />} />
        <Route path="carrito" element={<StorefrontCart />} />
        <Route path="checkout" element={<StorefrontCheckout />} />
        <Route path="login" element={<StorefrontLogin />} />
        <Route path="registro" element={<StorefrontRegister />} />
        <Route path="mis-pedidos" element={<StorefrontOrders />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
