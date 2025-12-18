import { Routes } from '@angular/router';
import { Home } from './home/home';
import { MascotaFormComponent } from './mascota/mascota-form/mascota-form.component';
import { MascotaEditComponent } from './mascota/mascota-edit/mascota-edit.component';
import { MascotaListComponent } from './mascota/mascota-list/mascota-list.component';
import { AvistamientosComponent } from './avistamientos/avistamientos';
import { ReportarAvistamientoComponent } from './reportar-avistamiento/reportar-avistamiento';
import { LoginComponent } from './login/login';
import { RegistroComponent } from './registro/registro';
import { PerfilComponent } from './perfil/perfil';
import { AccesoDenegadoComponent } from './acceso-denegado/acceso-denegado';
import { authGuard } from './guards/auth.guard';
import { MisPublicacionesComponent } from './mis-publicaciones/mis-publicaciones.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: Home
  },
  {
    path: 'mascotas',
    component: MascotaListComponent
  },
  {
    path: 'mascota/nuevo',
    component: MascotaFormComponent
  },
  {
    path: 'mascota/:id/editar',
    component: MascotaEditComponent
  },
  {
    path: 'mis-publicaciones',
    component: MisPublicacionesComponent,
    canActivate: [authGuard]
  },
  {
    path: 'avistamientos',
    component: AvistamientosComponent
  },
  {
    path: 'reportar-avistamiento/:id',
    component: ReportarAvistamientoComponent,
    canActivate: [authGuard]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'registro',
    component: RegistroComponent
  },
  {
    path: 'perfil',
    component: PerfilComponent,
    canActivate: [authGuard]
  },
  {
    path: 'acceso-denegado',
    component: AccesoDenegadoComponent
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
