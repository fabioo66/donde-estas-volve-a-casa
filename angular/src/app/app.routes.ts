import { Routes } from '@angular/router';
import { Home } from './home/home';
import { LoginComponent } from './login/login';
import { RegistroComponent } from './registro/registro';
import { PerfilComponent } from './perfil/perfil';

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
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'registro',
    component: RegistroComponent
  },
  {
    path: 'perfil',
    component: PerfilComponent
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
