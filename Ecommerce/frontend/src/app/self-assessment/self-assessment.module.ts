import { NgSelectModule } from '@ng-select/ng-select';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { AssessmentModule } from './assessment/assessment.module';
import { ExpertModule } from './expert-assessment/expert.module';
import { DetailAssessmentComponent } from './assessment/detail-assessment/detail-assessment.component';


// routing
const routes: Routes = [
  {
    path: 'self',
    loadChildren: () => import('./assessment/assessment.module').then(m => m.AssessmentModule)
  },
  {
    path: 'expert',
    loadChildren: () => import('./expert-assessment/expert.module').then(m => m.ExpertModule)
  },
  {
    path: 'report-criteria',
    loadChildren: () => import('./criteria/assessment.module').then(m => m.AssessmentModule)
  },
  {
    path: 'standard-report',
    loadChildren: () => import('./standard/assessment.module').then(m => m.AssessmentModule)
  }
];

@NgModule({
  declarations: [
 
  
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    AssessmentModule,
    ExpertModule,
    NgSelectModule
  ],
  exports:[
    DetailAssessmentComponent,
  ]
})
export class SelfAssessmentModule { }
