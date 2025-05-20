import { FormEvent, useState } from "react";
import { Button } from "../../../../components/Button/Button";
import { Input } from "../../../../components/Input/Input";
import { request } from "../../../../utils/api";
import { IUser } from "../../../authentication/contexts/AuthenticationContextProvider";
import classes from "./Education.module.scss";

interface EducationItem {
  id: number;
  school: string;
  degree: string;
  fieldOfStudy: string;
  startDate: string;
  endDate?: string;
  description?: string;
}

interface EducationProps {
  user: IUser | null;
  authUser: IUser | null;
}

export function Education({ user, authUser }: EducationProps) {
  const [educations, setEducations] = useState<EducationItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [editing, setEditing] = useState<number | null>(null);
  const [error, setError] = useState("");
  
  const [formData, setFormData] = useState({
    school: "",
    degree: "",
    fieldOfStudy: "",
    startDate: "",
    endDate: "",
    description: ""
  });

  // Fetch educations when component mounts
  useState(() => {
    if (user?.id) {
      request<EducationItem[]>({
        endpoint: `/api/v1/profile/${user.id}/educations`,
        onSuccess: (data) => {
          setEducations(data);
          setLoading(false);
        },
        onFailure: (error) => {
          console.error(error);
          setLoading(false);
        }
      });
    }
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const resetForm = () => {
    setFormData({
      school: "",
      degree: "",
      fieldOfStudy: "",
      startDate: "",
      endDate: "",
      description: ""
    });
    setError("");
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    
    if (!formData.school || !formData.degree || !formData.startDate) {
      setError("Please fill all required fields");
      return;
    }

    try {
      if (editing !== null) {
        // Update existing education
        request<EducationItem>({
          endpoint: `/api/v1/profile/educations/${editing}`,
          method: "PUT",
          body: JSON.stringify(formData),
          onSuccess: (updatedEducation) => {
            setEducations(educations.map(edu => 
              edu.id === editing ? updatedEducation : edu
            ));
            setEditing(null);
            resetForm();
            setAdding(false);
          },
          onFailure: (error) => {
            setError(error);
          }
        });
      } else {
        // Add new education
        request<EducationItem>({
          endpoint: `/api/v1/profile/${user?.id}/educations`,
          method: "POST",
          body: JSON.stringify(formData),
          onSuccess: (newEducation) => {
            setEducations([...educations, newEducation]);
            setAdding(false);
            resetForm();
          },
          onFailure: (error) => {
            setError(error);
          }
        });
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    }
  };

  const handleEdit = (education: EducationItem) => {
    setFormData({
      school: education.school,
      degree: education.degree,
      fieldOfStudy: education.fieldOfStudy,
      startDate: education.startDate,
      endDate: education.endDate || "",
      description: education.description || ""
    });
    setEditing(education.id);
    setAdding(true);
  };

  const handleDelete = (id: number) => {
    request<void>({
      endpoint: `/api/v1/profile/educations/${id}`,
      method: "DELETE",
      onSuccess: () => {
        setEducations(educations.filter(edu => edu.id !== id));
      },
      onFailure: (error) => {
        setError(error);
      }
    });
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long' });
  };

  return (
    <div className={classes.education}>
      <div className={classes.header}>
        <h2>Education</h2>
        {user?.id === authUser?.id && !adding && (
          <button 
            className={classes.action} 
            onClick={() => {
              setAdding(true);
              setEditing(null);
              resetForm();
            }}
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512">
              <path d="M256 80c0-17.7-14.3-32-32-32s-32 14.3-32 32V224H48c-17.7 0-32 14.3-32 32s14.3 32 32 32H192V432c0 17.7 14.3 32 32 32s32-14.3 32-32V288H400c17.7 0 32-14.3 32-32s-14.3-32-32-32H256V80z"/>
            </svg>
          </button>
        )}
      </div>

      {adding ? (
        <form className={classes.form} onSubmit={handleSubmit}>
          <Input
            label="School *"
            name="school"
            value={formData.school}
            onChange={handleInputChange}
            placeholder="e.g. Stanford University"
          />
          
          <div className={classes.row}>
            <Input
              label="Degree *"
              name="degree"
              value={formData.degree}
              onChange={handleInputChange}
              placeholder="e.g. Bachelor's"
            />
            <Input
              label="Field of Study"
              name="fieldOfStudy"
              value={formData.fieldOfStudy}
              onChange={handleInputChange}
              placeholder="e.g. Computer Science"
            />
          </div>
          
          <div className={classes.row}>
            <Input
              label="Start Date *"
              name="startDate"
              type="date"
              value={formData.startDate}
              onChange={handleInputChange}
            />
            <Input
              label="End Date"
              name="endDate"
              type="date"
              value={formData.endDate}
              onChange={handleInputChange}
            />
          </div>
          
          <div className={classes.fullRow}>
            <label>Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              rows={4}
              placeholder="Describe your studies and achievements"
            />
          </div>
          
          {error && <p className={classes.error}>{error}</p>}
          
          <div className={classes.buttons}>
            <Button 
              outline 
              onClick={() => {
                setAdding(false);
                setEditing(null);
                resetForm();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              {editing !== null ? 'Update' : 'Add'}
            </Button>
          </div>
        </form>
      ) : (
        <div className={classes.list}>
          {educations.length > 0 ? (
            educations.map((education) => (
              <div key={education.id} className={classes.item}>
                <div className={classes.icon}>🎓</div>
                <div className={classes.content}>
                  <h3 className={classes.school}>{education.school}</h3>
                  <div className={classes.degree}>
                    {education.degree} {education.fieldOfStudy && `in ${education.fieldOfStudy}`}
                  </div>
                  <div className={classes.dates}>
                    {formatDate(education.startDate)} - {education.endDate ? formatDate(education.endDate) : 'Present'}
                  </div>
                  {education.description && (
                    <div className={classes.description}>{education.description}</div>
                  )}
                  
                  {user?.id === authUser?.id && (
                    <div className={classes.actions}>
                      <button 
                        className={classes.action} 
                        onClick={() => handleEdit(education)}
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
                          <path d="M362.7 19.3L314.3 67.7 444.3 197.7l48.4-48.4c25-25 25-65.5 0-90.5L453.3 19.3c-25-25-65.5-25-90.5 0zm-71 71L58.6 323.5c-10.4 10.4-18 23.3-22.2 37.4L1 481.2C-1.5 489.7 .8 498.8 7 505s15.3 8.5 23.7 6.1l120.3-35.4c14.1-4.2 27-11.8 37.4-22.2L421.7 220.3 291.7 90.3z" />
                        </svg>
                      </button>
                      <button 
                        className={classes.action} 
                        onClick={() => handleDelete(education.id)}
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512">
                          <path d="M135.2 17.7L128 32H32C14.3 32 0 46.3 0 64S14.3 96 32 96H416c17.7 0 32-14.3 32-32s-14.3-32-32-32H320l-7.2-14.3C307.4 6.8 296.3 0 284.2 0H163.8c-12.1 0-23.2 6.8-28.6 17.7zM416 128H32L53.2 467c1.6 25.3 22.6 45 47.9 45H346.9c25.3 0 46.3-19.7 47.9-45L416 128z"/>
                        </svg>
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))
          ) : (
            <p>No education added yet.</p>
          )}
          
          {user?.id === authUser?.id && !adding && (
            <button 
              className={classes.addButton} 
              onClick={() => {
                setAdding(true);
                resetForm();
              }}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 448 512">
                <path fill="currentColor" d="M256 80c0-17.7-14.3-32-32-32s-32 14.3-32 32V224H48c-17.7 0-32 14.3-32 32s14.3 32 32 32H192V432c0 17.7 14.3 32 32 32s32-14.3 32-32V288H400c17.7 0 32-14.3 32-32s-14.3-32-32-32H256V80z"/>
              </svg>
              Add education
            </button>
          )}
        </div>
      )}
    </div>
  );
}