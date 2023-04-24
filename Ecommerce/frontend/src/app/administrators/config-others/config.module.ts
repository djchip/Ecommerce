import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ListConfigComponent } from './list-config/list-config.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { ListDocumentTypeComponent } from './document-type-config/list-document-type/list-document-type.component';
import { AddDocumentTypeComponent } from './document-type-config/add-document-type/add-document-type.component';
import { EditDocumentTypeComponent } from './document-type-config/edit-document-type/edit-document-type.component';
import { AddFieldComponent } from './field-config/add-field/add-field.component';
import { EditFieldComponent } from './field-config/edit-field/edit-field.component';
import { ListFieldComponent } from './field-config/list-field/list-field.component';
import { ListExhCodeComponent } from './exh-code-config/list-exh-code/list-exh-code.component';
import { AddExhCodeComponent } from './exh-code-config/add-exh-code/add-exh-code.component';
import { EditExhCodeComponent } from './exh-code-config/edit-exh-code/edit-exh-code.component';
import { ListDateFormatComponent } from './date-format/list-date-format/list-date-format.component';
import { AddDateFormatComponent } from './date-format/add-date-format/add-date-format.component';
import { EditDateFormatComponent } from './date-format/edit-date-format/edit-date-format.component';
import { DetailDocumentComponent } from './document-type-config/detail-document/detail-document.component';
import { DetailExhCodeComponent } from './exh-code-config/detail-exh-code/detail-exh-code.component';


const routes: Routes = [
  {
    path: 'list',
    component: ListConfigComponent, 
  },
  {
    path: 'document-type',
    component: ListDocumentTypeComponent,
  },
  {
    path: 'field',
    component: ListFieldComponent,
  },
  {
    path: 'exh-code',
    component: ListExhCodeComponent,
  },
  {
    path: 'date-format',
    component: ListDateFormatComponent,
  }

];

@NgModule({
  declarations: [
    ListConfigComponent,
    ListDocumentTypeComponent,
    AddDocumentTypeComponent,
    EditDocumentTypeComponent,
    AddFieldComponent,
    EditFieldComponent,
    ListFieldComponent,
    ListExhCodeComponent,
    AddExhCodeComponent,
    EditExhCodeComponent,
    ListDateFormatComponent,
    AddDateFormatComponent,
    EditDateFormatComponent,
    DetailDocumentComponent,
    DetailExhCodeComponent,

  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    ContentHeaderModule,
    TranslateModule
  ],
  exports:[
    DetailDocumentComponent,
    DetailExhCodeComponent
  ]
})
export class ConfigModule { }
