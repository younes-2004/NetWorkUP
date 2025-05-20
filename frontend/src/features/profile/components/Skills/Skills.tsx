import { FormEvent, useState } from "react";
import { Button } from "../../../../components/Button/Button";
import { Input } from "../../../../components/Input/Input";
import { request } from "../../../../utils/api";
import { IUser } from "../../../authentication/contexts/AuthenticationContextProvider";
import classes from "./Skills.module.scss";

interface SkillItem {
  id: number;
  name: string;
  endorsements: number;
}

interface SkillsProps {
  user: IUser | null;
  authUser: IUser | null;
}

export function Skills({ user, authUser }: SkillsProps) {
  const [skills, setSkills] = useState<SkillItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [error, setError] = useState("");
  const [skillName, setSkillName] = useState("");
  
  // Fetch skills when component mounts
  useState(() => {
    if (user?.id) {
      request<SkillItem[]>({
        endpoint: `/api/v1/profile/${user.id}/skills`,
        onSuccess: (data) => {
          setSkills(data);
          setLoading(false);
        },
        onFailure: (error) => {
          console.error(error);
          setLoading(false);
        }
      });
    }
  });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    
    if (!skillName.trim()) {
      setError("Please enter a skill name");
      return;
    }

    try {
      request<SkillItem>({
        endpoint: `/api/v1/profile/${user?.id}/skills`,
        method: "POST",
        body: JSON.stringify({ name: skillName }),
        onSuccess: (newSkill) => {
          setSkills([...skills, newSkill]);
          setAdding(false);
          setSkillName("");
          setError("");
        },
        onFailure: (error) => {
          setError(error);
        }
      });
    } catch (err) {
      setError("An error occurred. Please try again.");
    }
  };

  const handleDelete = (id: number) => {
    request<void>({
      endpoint: `/api/v1/profile/skills/${id}`,
      method: "DELETE",
      onSuccess: () => {
        setSkills(skills.filter(skill => skill.id !== id));
      },
      onFailure: (error) => {
        setError(error);
      }
    });
  };

  const handleEndorse = (id: number) => {
    request<SkillItem>({
      endpoint: `/api/v1/profile/skills/${id}/endorse`,
      method: "POST",
      onSuccess: (updatedSkill) => {
        setSkills(skills.map(skill => 
          skill.id === id ? updatedSkill : skill
        ));
      },
      onFailure: (error) => {
        setError(error);
      }
    });
  };

  return (
    <div className={classes.skills}>
      <div className={classes.header}>
        <h2>Skills</h2>
        {user?.id === authUser?.id && !adding && (
          <button 
            className={classes.action} 
            onClick={() => {
              setAdding(true);
              setSkillName("");
              setError("");
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
            label="Skill *"
            value={skillName}
            onChange={(e) => setSkillName(e.target.value)}
            placeholder="e.g. JavaScript"
          />
          
          {error && <p className={classes.error}>{error}</p>}
          
          <div className={classes.buttons}>
            <Button 
              outline 
              onClick={() => {
                setAdding(false);
                setSkillName("");
                setError("");
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              Add
            </Button>
          </div>
        </form>
      ) : (
        <div className={classes.list}>
          {skills.length > 0 ? (
            skills.map((skill) => (
              <div key={skill.id} className={classes.skill}>
                <span>{skill.name}</span>
                
                {skill.endorsements > 0 && (
                  <span className={classes.endorsements}>{skill.endorsements}</span>
                )}
                
                {user?.id === authUser?.id ? (
                  <button 
                    className={classes.delete} 
                    onClick={() => handleDelete(skill.id)}
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512">
                      <path d="M342.6 150.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L192 210.7 86.6 105.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L146.7 256 41.4 361.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L192 301.3 297.4 406.6c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L237.3 256 342.6 150.6z"/>
                    </svg>
                  </button>
                ) : (
                  <button 
                    className={`${classes.endorse}`} 
                    onClick={() => handleEndorse(skill.id)}
                    disabled={user?.id === authUser?.id}
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 512 512">
                      <path fill="currentColor" d="M313.4 32.9c26 5.2 42.9 30.5 37.7 56.5l-2.3 11.4c-5.3 26.7-15.1 52.1-28.8 75.2H464c26.5 0 48 21.5 48 48c0 18.5-10.5 34.6-25.9 42.6C497 275.4 504 288.9 504 304c0 23.4-16.8 42.9-38.9 47.1c4.4 7.3 6.9 15.8 6.9 24.9c0 21.3-13.9 39.4-33.1 45.6c.7 3.3 1.1 6.8 1.1 10.4c0 26.5-21.5 48-48 48H294.5c-19 0-37.5-5.6-53.3-16.1l-38.5-25.7C176 420.4 160 390.4 160 358.3V320 272 247.1c0-29.2 13.3-56.7 36-75l7.4-5.9c26.5-21.2 44.6-51 51.2-84.2l2.3-11.4c5.2-26 30.5-42.9 56.5-37.7zM32 192H96c17.7 0 32 14.3 32 32V448c0 17.7-14.3 32-32 32H32c-17.7 0-32-14.3-32-32V224c0-17.7 14.3-32 32-32z"/>
                    </svg>
                    Endorse
                  </button>
                )}
              </div>
            ))
          ) : (
            <p>No skills added yet.</p>
          )}
          
          {user?.id === authUser?.id && !adding && (
            <button 
              className={classes.addButton} 
              onClick={() => {
                setAdding(true);
                setSkillName("");
              }}
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 448 512">
                <path fill="currentColor" d="M256 80c0-17.7-14.3-32-32-32s-32 14.3-32 32V224H48c-17.7 0-32 14.3-32 32s14.3 32 32 32H192V432c0 17.7 14.3 32 32 32s32-14.3 32-32V288H400c17.7 0 32-14.3 32-32s-14.3-32-32-32H256V80z"/>
              </svg>
              Add skill
            </button>
          )}
        </div>
      )}
    </div>
  );
}