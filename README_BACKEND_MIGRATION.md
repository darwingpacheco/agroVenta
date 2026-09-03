# Migracion inicial a backend APIs

Este documento resume la migracion funcional aplicada al proyecto Android.

## Cambios implementados

1. Inicio sin bloqueo de autenticacion:
   - `Home` ahora permite entrar a ver productos sin login.
   - El acceso a cuenta queda opcional.

2. Autenticacion sin Firebase:
   - Login/registro/reset migrados a `BackendRepository` + Retrofit (`AgroVentaApi`).
   - Sesion basada en token en `SessionManager`.

3. Compra con ciudades principales:
   - Se elimino direccion de envio.
   - Checkout usa spinner de ciudades (`dispatchCities`).

4. Logistica de despacho consolidado:
   - Se agrega evaluacion de consolidacion por ciudad (`DispatchPlan`).
   - Umbral base: minimo 2000 kg, maximo 5000 kg por viaje.
   - Si no llega al minimo: pedido queda en consolidacion con alternativa de envio individual.

5. Pasarela de pagos (base tecnica):
   - Se agrega flujo de `payment intent` en checkout (`createPaymentIntent`).
   - Metodo de pago via spinner (`paymentMethods`).

6. Flujo de venta alineado con backend:
   - Publicacion de producto ahora va por API (`publishProduct`).
   - Publicar requiere usuario autenticado.

7. Perfil con ventas realizadas:
   - `DetailUser` ahora muestra compras y resumen de ventas.

## Estructura nueva agregada

- `app/src/main/java/com/example/agroventa/network/`
  - `ApiClient.java`
  - `AgroVentaApi.java`
- `app/src/main/java/com/example/agroventa/repository/`
  - `BackendRepository.java`
- `app/src/main/java/com/example/agroventa/data/`
  - `PurchaseRequest.java`
  - `DispatchPlan.java`
  - `SaleRecord.java`
  - `UserProfile.java`

## Modo backend

En `app/build.gradle`:
- `BACKEND_BASE_URL`
- `USE_MOCK_BACKEND`

Actualmente `USE_MOCK_BACKEND = true` para permitir ejecutar el flujo sin backend real.
Cuando tengas backend listo, cambia a `false` y ajusta `BACKEND_BASE_URL`.

## Endpoints esperados (contrato inicial)

- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/password-reset`
- `GET /products?category=`
- `POST /products`
- `POST /payments/intent`
- `POST /orders`
- `GET /users/profile`
- `GET /users/sales`

## Notas de siguiente fase

- Implementar webhooks/confirmacion real de pasarela (estado pago retenido/liberado).
- Persistir estado de consolidacion por ciudad en backend (no solo cliente).
- En ventas, separar explicitamente origen de cosecha y ciudad objetivo de despacho por lote.
- Reemplazar mock de imagenes por subida a storage del backend + URLs firmadas.

