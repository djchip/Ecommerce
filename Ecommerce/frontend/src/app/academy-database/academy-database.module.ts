import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'app/auth/helpers/auth.guards';
import { DocumentModule } from './document/document.module';
import { FormModule } from './form-management/form.module';
import { UnitManagementModule } from './unit-managerment/unit-managerment.module';
import { DetailFormComponent } from './form-management/detail-form/detail-form.component';
import { DetailDocumentComponent } from './document/detail-document/detail-document.component';
import { DetailUnitComponent } from './unit-managerment/detail-unit/detail-unit.component';
import { DatabaseModule } from './database/database.module';
import { ViewImportUnitComponent } from './unit-managerment/view-import-unit/view-import-unit.component';


// routing
const routes: Routes = [
  {
    path: 'unit',
    loadChildren: () => import('./unit-managerment/unit-managerment.module').then(m => m.UnitManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'form',
    loadChildren: () => import('./form-management/form.module').then(m => m.FormModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'document',
    loadChildren: () => import('./document/document.module').then(m => m.DocumentModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'academy',
    loadChildren: () => import('./database/database.module').then(m => m.DatabaseModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'statistical',
    loadChildren: () => import('./statistical/statistical.module').then(m => m.StatisticalModule),
    canActivate: [AuthGuard]
  }
];

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    DocumentModule,
    FormModule,
    UnitManagementModule,
    DatabaseModule,
  ],
  exports:[
    DetailFormComponent,
    DetailDocumentComponent,  
    DetailUnitComponent,
    ViewImportUnitComponent,
  ]
})
export class AcademyDatabaseModule { }
