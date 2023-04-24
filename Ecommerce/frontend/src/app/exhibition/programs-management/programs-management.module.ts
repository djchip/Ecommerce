import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AddProgramsComponent } from './add-programs/add-programs.component';
import { ListProgramsComponent } from './list-programs/list-programs.component';
import { EditProgramsComponent } from './edit-programs/edit-programs.component';
import { RouterModule, Routes } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { DetailProgramsComponent } from './detail-programs/detail-programs.component';
import { TranslateModule } from '@ngx-translate/core';

const routes: Routes = [
  {
    path: 'list',
    component: ListProgramsComponent,
    data: { animation: 'feather' }
  }
];


@NgModule({
  declarations: [
    ListProgramsComponent,
    AddProgramsComponent,
    EditProgramsComponent,
    DetailProgramsComponent,


  ],
  imports: [
    CommonModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    CorePipesModule,
    ReactiveFormsModule,
    FormsModule,
    NgSelectModule,
    ContentHeaderModule,
    RouterModule.forChild(routes),
    TranslateModule

  ],
  exports: [
    DetailProgramsComponent
  ],
  providers:[
    DatePipe
  ]
})
export class ProgramsManagementModule { }
