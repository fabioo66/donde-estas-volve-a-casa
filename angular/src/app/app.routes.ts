import { Routes } from '@angular/router';
import { Home } from './home/home';
import { AvistamientosComponent } from './avistamientos/avistamientos';
import { ReportarAvistamientoComponent } from './reportar-avistamiento/reportar-avistamiento';

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
    path: 'reportar-avistamiento/:id',
    component: ReportarAvistamientoComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
