# Backoffice Admin Portal Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an isolated, high-performance Backoffice Admin Portal SPA in `admin/` to manage products, categories, brands, orders, and search indexing for the TechStore platform.

**Architecture:** Vue 3 + Vite + TypeScript SPA with TailwindCSS, Lucide icons, Pinia state management, Axios with JWT interceptors, and Vue Router with strict role-based authentication guards (`ADMIN`).

**Tech Stack:** Vue 3.5, Vite 5.4, TypeScript 5.9, Pinia 2.2, TailwindCSS 3.4, Lucide Vue Next, Vee-Validate / Zod, Vitest.

---

### Task 1: Project Initialization & Tooling Setup
- Package configuration, Vite + PostCSS + TailwindCSS setup.
- Entry point `main.ts`, `App.vue`, `style.css`.
- Verification via `npm run build`.

### Task 2: Types, API Client & Role-based Authentication
- Domain interfaces (`ApiResponse`, `PageResponse`, `User`, `Role`, `Product`, etc.).
- Axios client with JWT request interceptor & 401 response handling.
- Pinia `useAdminAuthStore` verifying `role === 'ADMIN'`.
- `LoginView.vue` with form validation.
- Router with navigation guard.

### Task 3: Admin Layout & Navigation Shell
- `AdminLayout.vue` with Sidebar and Header.
- Reusable UI: `StatCard.vue`, `ConfirmModal.vue`, `BadgeStatus.vue`.

### Task 4: Dashboard Overview & Key Metrics
- `DashboardView.vue` with KPI cards, quick actions, recent orders.

### Task 5: Product Management (CRUD & Specs Editor)
- `ProductListView.vue` with filters, search, pagination, delete confirm.
- `ProductFormView.vue` with image URLs and dynamic specifications editor (`ProductSpecsEditor.vue`).

### Task 6: Category & Brand Management
- `CategoryListView.vue` with tree/table and modal.
- `BrandListView.vue` with logo preview and modal.

### Task 7: Order Management & Processing Workflow
- `OrderListView.vue` with tabbed status filters.
- `OrderDetailModal.vue` with customer info, items, status transition and cancel actions.

### Task 8: System Operations & Elasticsearch Reindexing
- `SystemOpsView.vue` with trigger sync Elasticsearch (`/api/v1/admin/search/reindex`).

### Task 9: End-to-End Verification & Documentation
- Full test pass (`npm run test`), production build check, and docs.
