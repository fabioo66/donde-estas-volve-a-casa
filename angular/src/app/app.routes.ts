import { Routes } from '@angular/router';
import { Home } from './home/home';
import { AvistamientosComponent } from './avistamientos/avistamientos';
import { ReportarAvistamientoComponent } from './reportar-avistamiento/reportar-avistamiento';
import { LoginComponent } from './login/login';
import { RegistroComponent } from './registro/registro';
import { PerfilComponent } from './perfil/perfil';
import { AccesoDenegadoComponent } from './acceso-denegado/acceso-denegado';
import { authGuard } from './guards/auth.guard';

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
