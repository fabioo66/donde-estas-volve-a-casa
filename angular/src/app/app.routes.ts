import { Routes } from '@angular/router';
import { Home } from './home/home';
import { AvistamientosComponent } from './avistamientos/avistamientos';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'avistamientos',
    component: AvistamientosComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
